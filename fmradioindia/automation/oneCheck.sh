find ../app/src/ -iname "*.java" | while read -r line; do echo "Processing $line ...";java -jar google-java-format-1.5-all-deps.jar --replace $line; done 
