## Howto
- intellij에서 실행하기 위해서는 library에 spark/jars 디렉토리를 추가해야 한다
- spark-submit으로

    bin/spark-submit --class "SimpleApp" --master local[4] build/libs/spark-first-1.0-SNAPSHOT.jar