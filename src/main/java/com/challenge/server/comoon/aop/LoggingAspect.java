package com.challenge.server.comoon.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

	/**
	 * com.ringle.server 하위의 모든 메소드 실행 시 로그 출력하는 포인트컷.
	 * 필요에 따라 pointcut expression 을 조정할 수 있습니다.
	 */
	@Around("execution(* com.ringle.server..*(..))")
	public Object logMethodEntry(ProceedingJoinPoint joinPoint) throws Throwable {
		// 해당 메소드가 속한 클래스의 로거를 생성 (클래스별 로깅)
		Logger log = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();

		// 메소드 진입 시 log.trace
		log.trace("Entering {}.{}", className, methodName);

		// 원래 메소드 실행
		Object result = joinPoint.proceed();

		return result;
	}
}

