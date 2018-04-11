find ../app/src/ -name "*.java" | xargs -L1 java -jar google-java-format-1.5-all-deps.jar --replace



../gradlew clean
/usr/local/Cellar/infer/0.13.0/bin/infer -- ../gradlew build

