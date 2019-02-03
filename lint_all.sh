#find $1  -name "*.java" | xargs -L1 java -jar ./preCommitCheck/google-java-format-1.5-all-deps.jar --replace
#find ./*/*/app/src -name "*.java" | xargs -L1 java -jar ./preCommitCheck/google-java-format-1.5-all-deps.jar --replace
find $1/app/src/main/java -name "*.java"
find $1/app/src/main/java -name "*.java" | xargs -L1 java -jar ./preCommitCheck/google-java-format-1.5-all-deps.jar --replace
