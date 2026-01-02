package com.example.tomo.firebase;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final FirebaseService firebaseService;

    public FirebaseAuthenticationFilter(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 로그인 전용 API만 처리
        if (!path.equals("/api/auth/firebase-login")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Preflight 요청(CORS OPTIONS)은 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String idToken = header.substring(7);
            try {
                // Firebase ID Token 검증
                FirebaseToken decodedToken = firebaseService.verifyIdToken(idToken);
                String uuid = decodedToken.getUid();
                System.out.println(" Firebase Token verified: " + uuid);

                // SecurityContext에 사용자 UID 등록
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(uuid, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);

                //  여기서 response를 직접 작성하지 않고 filterChain으로 넘김
                filterChain.doFilter(request, response);
                return;

            } catch (FirebaseAuthException e) {
                System.out.println("[DEBUG] Token verification failed: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired Firebase ID token");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization header");
            return;
        }
    }
}
