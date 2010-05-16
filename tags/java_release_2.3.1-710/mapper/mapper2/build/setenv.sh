BUILD_CP=$CLASSPATH:$JAVA_HOME/lib/tools.jar
for i in ../devlib/*.jar; do
    BUILD_CP=$BUILD_CP:$i
done
