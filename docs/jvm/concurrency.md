# JVM에서 동시성

## 자바 개발자를 위한 97가지 제안 중 82번째 제안

스레드는 인프라스트럭처로 취급해야 한다.

프로그래밍하면서 스택 사용을 관리하는 (또는 생각이라도 하는) 자바 프로그래머가 몇이나 될까? 아마 거의 없을 것이다. 거의 모든 자바 프로그래머는 스택 관리를 컴파일러와 런타임 시스템에 맡기는 편이다.

프로그래밍하면서 힙 메모리를 관리하는 (또는 생각이라도 하는) 자바 프로그래머가 몇이나 될까? 극히 적을 것이다. 거의 모든 자바 프로그래머는 가비지 컬렉션 시스템이 모든 힙 관리를 책임질 것이라고 생각한다.

그러면 왜 그렇게 많은 자바 프로그래머가 스레드를 직접 관리하는 걸까? 왜냐하면 그렇게 하도록 교육받았기 때문이다. 애초에 자바는 공유 메모리 멀티스레딩을 지원했다. 확실히 잘못된 결정이었다.

대부분 자바 프로그래머가 알고 있는 동시성(concurrency)과 병렬성(parallelism)은 1960년대 운영체제 구축 이론을 바탕으로 한다. 운영체제를 개발한다면 당연히 알아 두면 좋을 것이지만 대부분 자바 프로그램이 운영체제인가? 아니다. 그러므로 다시 생각해 봐야 한다.

만일 코드가 동기화 구문, lock, mutext 등(모두 운영체제를 위한 것이다)을 사용한다면 뭔가 잘못하고 있을 가능성이 크다. 앞서 언급한 것은 대부분 자바 프로그래머에게 잘못된 추상화 수준이다. **스택 공간과 힙 공간이 관리형 리소스인 것처럼 스레드 역시 관리형 리소스여야 한다**. 스레드를 명시적으로 생성하고 관리하는 것이 아니라 태스크를 생성하고 스레드 풀에 제출하는 방식이어야 한다. 그리고 당연하겠지만 태스크는 단일 스레드여야 한다! **여러 태스크가 서로 통신해야 한다면 공유 메모리 대신 스레드에 안전한 큐(queue)를 사용해야 한다**. 이런 내용은 이미 1970년대에 알려졌으며, 찰스 앤터니 리처드 호어 경(Sir Charles Antony Richard Hoare)이 동시 및 병렬 연산을 서술하기 위한 대수학으로 **순차 통신 프로세스(CSP, Communicating Sequential Process)**를 창시하는 데 결정적 영향을 미쳤다. **슬프게도 대부분 프로그래머는 공유 메모리 멀티스레딩을 사용하기 급급해서 이 이론을 무시했으며 모든 프로그램이 마치 새로운 운영체제처럼 만들어져 왔다**. 하지만 2000년대에 들어서면서 많은 프로그래머가 CSP를 되돌아보기 시작했다. 아마 최근 몇 년 사이에 이 이론을 가장 지지하는 것은 Go 언어일 것이다. Go는 CSP를 적극 도입해서 기반 스레드 풀을 통해 실행하도록 만들어졌다.

많은 사람이 사용하는 액터(Actor), 데이터플로우(dataflow), CSP, 활성 객체(active ojbect)는 모두 순차 프로세스와 통신을 일컫는 용어들이다. 아카(Akka), 콰사르(Quasar), 지파스(GParse)는 스레드 풀에서 태스크를 실행하는 기능을 제공하는 프레임워크다. 자바 플랫폼은 자바 8부터 스트림 라이브러리를 기반으로 한 Fork/Join 프레임워크를 제공한다.

대부분 자바 프로그래머에게 올바른 추상화 수준을 제공하려면 스레드는 관리형 리소스여야 한다. 액터, 데이터플로우, CSP, 활성 객체는 대부분 프로그래머가 사용하는 추상화다. 스레드를 손수 관리할 필요가 없으면 자바 프로그래머는 더 간단하고 더 포괄적이며 더 쉽게 유지보수할 수 있는 시스템을 작성할 수 있다.
