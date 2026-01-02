package com.markoala.tomoandroid.utils

import com.markoala.tomoandroid.ui.components.ToastManager

/**
 * 에러 메시지를 분석하여 적절한 사용자 친화적 메시지와 토스트 타입을 결정하는 유틸리티 클래스
 */
object ErrorHandler {

    enum class ToastType {
        INFO, WARNING, ERROR
    }

    data class ErrorResult(
        val message: String,
        val toastType: ToastType
    )

    /**
     * 친구 검색 관련 에러를 처리합니다.
     */
    fun handleFriendSearchError(error: String): ErrorResult {
        return when {
            error.contains("찾을 수 없습니다") ||
                    error.contains("친구를 찾을 수 없습니다") ||
                    error.contains("사용자를 찾을 수 없습니다") -> {
                ErrorResult("해당 이메일로 등록된 사용자가 없습니다.", ToastType.INFO)
            }

            error.contains("네트워크") -> {
                ErrorResult("네트워크 연결을 확인해주세요.", ToastType.ERROR)
            }

            error.contains("인증") -> {
                ErrorResult("로그인이 필요합니다.", ToastType.ERROR)
            }

            error.contains("검색에 실패") -> {
                ErrorResult("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", ToastType.ERROR)
            }

            else -> {
                ErrorResult("검색 중 오류가 발생했습니다: $error", ToastType.ERROR)
            }
        }
    }

    /**
     * 친구 추가 관련 에러를 처리합니다.
     */
    fun handleFriendAddError(error: String): ErrorResult {
        return when {
            error.contains("이미 친구") || error.contains("already") -> {
                ErrorResult("이미 친구로 등록된 사용자입니다.", ToastType.INFO)
            }

            error.contains("찾을 수 없습니다") ||
                    error.contains("친구를 찾을 수 없습니다") ||
                    error.contains("사용자를 찾을 수 없습니다") -> {
                ErrorResult("해당 이메일로 등록된 사용자가 없습니다.", ToastType.INFO)
            }

            error.contains("자신을") || error.contains("본인") -> {
                ErrorResult("자신을 친구로 추가할 수 없습니다.", ToastType.WARNING)
            }

            error.contains("네트워크") -> {
                ErrorResult("네트워크 연결을 확인해주세요.", ToastType.ERROR)
            }

            error.contains("인증") -> {
                ErrorResult("로그인이 필요합니다.", ToastType.ERROR)
            }

            error.contains("친구 추가에 실패") -> {
                ErrorResult("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", ToastType.ERROR)
            }

            else -> {
                ErrorResult("친구 추가 중 오류가 발생했습니다: $error", ToastType.ERROR)
            }
        }
    }

    /**
     * HTTP 상태 코드에 따른 에러를 처리합니다.
     */
    fun handleHttpError(statusCode: Int, errorBody: String? = null): ErrorResult {
        return when (statusCode) {
            400 -> {
                ErrorResult("잘못된 요청입니다. 입력 정보를 확인해주세요.", ToastType.WARNING)
            }

            401 -> {
                ErrorResult("인증이 필요합니다. 다시 로그인해주세요.", ToastType.ERROR)
            }

            403 -> {
                ErrorResult("접근 권한이 없습니다.", ToastType.ERROR)
            }

            404 -> {
                ErrorResult("해당 이메일로 등록된 사용자가 없습니다.", ToastType.INFO)
            }

            409 -> {
                ErrorResult("이미 친구로 등록된 사용자입니다.", ToastType.INFO)
            }

            422 -> {
                ErrorResult("입력한 정보가 올바르지 않습니다.", ToastType.WARNING)
            }

            429 -> {
                ErrorResult("요청이 너무 많습니다. 잠시 후 다시 시도해주세요.", ToastType.WARNING)
            }

            500 -> {
                ErrorResult("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", ToastType.ERROR)
            }

            502 -> {
                ErrorResult("서버 연결에 문제가 있습니다. 잠시 후 다시 시도해주세요.", ToastType.ERROR)
            }

            503 -> {
                ErrorResult("서버가 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해주세요.", ToastType.ERROR)
            }

            504 -> {
                ErrorResult("서버 응답 시간이 초과되었습니다. 다시 시도해주세요.", ToastType.ERROR)
            }

            in 500..599 -> {
                ErrorResult("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", ToastType.ERROR)
            }

            else -> {
                ErrorResult("알 수 없는 오류가 발생했습니다. (코드: $statusCode)", ToastType.ERROR)
            }
        }
    }

    /**
     * ErrorResult를 기반으로 ToastManager에 적절한 토스트를 표시합니다.
     */
    fun showToast(toastManager: ToastManager, errorResult: ErrorResult) {
        when (errorResult.toastType) {
            ToastType.INFO -> toastManager.showInfo(errorResult.message)
            ToastType.WARNING -> toastManager.showWarning(errorResult.message)
            ToastType.ERROR -> toastManager.showError(errorResult.message)
        }
    }
}