package com.markoala.tomoandroid.data.api

import android.util.Log
import com.markoala.tomoandroid.auth.AuthManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

// 모든 네트워크 요청에 자동으로 인증 토큰(Access Token)을 추가하고, 401/419 응답 시 처리하는 인터셉터
class AuthInterceptor : Interceptor {

    companion object {
        private const val TAG = "AuthInterceptor"

        @Volatile
        private var isRefreshing = false
        private val refreshLock = Object()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 이미 Authorization 헤더가 있는 경우 그대로 진행 (Firebase 토큰 교환 시)
        if (originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        // Refresh-Token 헤더가 있는 경우 (토큰 갱신 요청) 그대로 진행
        if (originalRequest.header("Refresh-Token") != null) {
            return chain.proceed(originalRequest)
        }

        // 저장된 access token 가져오기
        val accessToken = AuthManager.getStoredAccessToken()

        val requestWithToken = if (accessToken != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        // 요청 실행
        val response = chain.proceed(requestWithToken)

        // 419 응답 처리 (Refresh Token 만료 - 재로그인 필요)
        if (response.code == 419) {
            Log.w(TAG, "419 Authentication Timeout - Refresh Token 만료, 재로그인 필요")
            response.close()

            // 토큰 삭제 및 로그아웃 콜백 호출
            runBlocking {
                AuthManager.clearTokens()
                AuthManager.triggerLogout()
            }

            // 419 응답 그대로 반환 (앱에서 로그인 화면으로 이동)
            return chain.proceed(requestWithToken)
        }

        // 401 응답 처리 (Access Token 만료 - 토큰 갱신)
        if (response.code == 401 && accessToken != null) {
            Log.w(TAG, "401 Unauthorized 응답 수신 - 토큰 갱신 시도")
            response.close() // 기존 응답 닫기

            // 토큰 갱신 시도 (동기적으로 처리)
            val newAccessToken = runBlocking {
                AuthManager.refreshAccessToken()
            }

            if (newAccessToken != null) {
                Log.d(TAG, "토큰 갱신 성공 - 원래 요청 재시도")
                // 갱신된 토큰으로 원래 요청 재시도
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
                return chain.proceed(newRequest)
            } else {
                Log.e(TAG, "토큰 갱신 실패 - 401 응답 반환")
                // 토큰 갱신 실패 시 원래의 401 응답 반환
                return chain.proceed(requestWithToken)
            }
        }

        return response
    }
}
