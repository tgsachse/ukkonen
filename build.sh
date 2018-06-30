javac source/SuffixTreeVisualizer/*.java
cd source
jar cvfm ../dist/$1.jar SuffixTreeVisualizer/manifest.txt SuffixTreeVisualizer/* Styles/*
rm SuffixTreeVisualizer/*.class
cd ..
