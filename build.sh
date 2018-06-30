# Build script for Suffix Tree Visualizer by Tiger Sachse

# If no name is provided, resort to a default, else use the name.
if [ -z "$1" ]
then
    NAME="SuffixTreeVisualizer"
else
    NAME=$1
fi

# Compile the source code, throw it all in a jar, and clean up.
cd source
javac SuffixTreeVisualizer/*.java
jar cvfm ../dist/$NAME.jar SuffixTreeVisualizer/manifest.txt SuffixTreeVisualizer/* Styles/*
rm SuffixTreeVisualizer/*.class
cd ..

echo ""
echo "Suffix Tree Visualizer packaged into jar located at dist/$NAME.jar"
