find ./fmradioindia/app/src/main/ -name "*.java" | xargs -L1 java -jar ./preCommitCheck/google-java-format-1.5-all-deps.jar --replace
find ./ping/client/app/src -name "*.java" | xargs -L1 java -jar ./preCommitCheck/google-java-format-1.5-all-deps.jar --replace
