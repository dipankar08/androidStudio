find ./*/app/src/main/ -name "*.java" | xargs -L1 java -jar ./preCommitCheck/google-java-format-1.5-all-deps.jar --replace
#find ./*/*/app/src -name "*.java" | xargs -L1 java -jar ./preCommitCheck/google-java-format-1.5-all-deps.jar --replace
