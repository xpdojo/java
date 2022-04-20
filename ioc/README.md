# Inversion of control

## 참조

- [전문가를 위한 스프링 5](https://www.aladin.co.kr/shop/wproduct.aspx?ISBN=9791160508864)

## IoC

의존성 주입(Dependency Injection)과 의존성 룩업(Dependency Lookup)

## Spring DI

스프링의 IoC 컨테이너 구현체를 말한다.
스프링 빈(Bean)을 정의하고 DI 요구사항을 만족시키는 `BeanFactory`는
애플리케이션에서 스프링을 조작할 때 사용하는 주요 인터페이스다.
`ApplicationContext`는 `BeanFactory`를 상속받아 더 강력한 기능을 제공한다.
