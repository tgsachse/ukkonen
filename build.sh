# Build script for Suffix Tree Visualizer by Tiger Sachse

# If no name is provided, resort to a default, else use the name.
if [ -z "$1" ]
then
    NAME="SuffixTreeVisualizer"
else
    NAME=$1
fi

# Copy the documentation.
cp -r docs source/Documentation

# Compile the source code, throw it all in a jar, and clean up.
cd source
javac SuffixTreeVisualizer/*.java
jar cvfm ../dist/$NAME.jar SuffixTreeVisualizer/Manifest.txt SuffixTreeVisualizer/* Styles/* Documentation/*
rm SuffixTreeVisualizer/*.class
cd ..

# Remove the copied documentation.
rm -r source/Documentation

echo ""
echo "Suffix Tree Visualizer packaged into jar located at dist/$NAME.jar"
