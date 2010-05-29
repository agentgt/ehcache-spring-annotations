die () {
    echo >&2 "$@"
    exit 1
}

[ "$#" -eq 2 ] || die "2 arguments required, $# provided"

OLD_REV=$1
NEW_REV=$2

# Copy and checkout previous site 
svn cp -m "Creating site for $NEW_REV" https://ehcache-spring-annotations.googlecode.com/svn/site/$OLD_REV https://ehcache-spring-annotations.googlecode.com/svn/site/$NEW_REV
svn co https://ehcache-spring-annotations.googlecode.com/svn/site/$NEW_REV

# Sync the new site over the previous site, removing files that no longer exist
rsync -avr --progress --exclude '.svn'  --delete /Users/edalquist/java/workspace/ehcache-spring-annotations_1.0-PATCHES/target/checkout/target/site/ ./$NEW_REV/

# Add new files into SVN control, this will generate a lot of 'is already under version control' warnings which can be ignored
find ./$NEW_REV/ -type f | grep -v .svn | xargs svn add

# Some reporting plugins generate output with inconsistent line endings, fix this
find ./$NEW_REV/ -type f | grep -v .svn | xargs dos2unix

# Commit the modified site
svn commit -m "Updating site for $NEW_REV" ./$NEW_REV
