# A collection of operations to manipulate this repository.

# Part of Willow by Tiger Sachse.
BUILD="build"
BUILD_STYLES="Styles"
BUILD_SOURCE="Source"
BUILD_PACKAGE="Willow"
BUILD_DOCS="Documentation"

REPO_DOCS="docs"
REPO_SOURCE="source"
REPO_STYLES="styles"
REPO_PACKAGE="willow"

DIST="dist"
MANIFEST="manifest.txt"
ENTRY_CLASS="GraphicalInterface"

# Build and run this project.
function run_project {
    build_project
    cd $BUILD

    java $BUILD_PACKAGE.$ENTRY_CLASS

    cd - &> /dev/null
    rm -rf $BUILD
}

# Build this project.
function build_project {
    rm -rf $BUILD

    mkdir -p $BUILD/$BUILD_PACKAGE
    mkdir -p $BUILD/$BUILD_STYLES

    cp $REPO_SOURCE/$REPO_STYLES/* $BUILD/$BUILD_STYLES/
    javac -d $BUILD $REPO_SOURCE/$REPO_PACKAGE/*.java
}

# Build and package this project.
function package_project {

    # If no name is provided, resort to a default.
    if [ -z "$1" ]
    then
        JAR_NAME=$BUILD_PACKAGE
    else
        JAR_NAME=$1
    fi

    # Build the project and create some necessary directories.
    build_project
    mkdir -p $BUILD/$BUILD_DOCS
    mkdir -p $BUILD/$BUILD_SOURCE
    mkdir -p $DIST
    
    cd $BUILD

    # Copy the project source and documentation into the build directory.
    cp -r ../$REPO_SOURCE/* $BUILD_SOURCE
    cp -r ../$REPO_DOCS/* $BUILD_DOCS

    # Create a manifest file and compress everything into a jar.
    echo "Main-Class: Willow.GraphicalInterface" > $MANIFEST
    jar cvfm ../$DIST/$JAR_NAME.jar $MANIFEST \
        $BUILD_SOURCE/* $BUILD_PACKAGE/* $BUILD_STYLES/* $BUILD_DOCS/*

    cd - &> /dev/null

    # Clean up after yourself.
    rm -r $BUILD
}

# Execute the appropriate function based on the first script argument.
case $1 in
    --run)
        run_project
        ;;
    --build)
        build_project
        ;;
    --pack)
        package_project $2
        ;;
esac
