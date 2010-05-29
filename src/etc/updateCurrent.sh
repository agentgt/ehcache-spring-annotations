die () {
    echo >&2 "$@"
    exit 1
}

[ "$#" -eq 1 ] || die "1 argument required, $# provided"

NEW_REV=$1

svn co https://ehcache-spring-annotations.googlecode.com/svn/site/current/
svn merge https://ehcache-spring-annotations.googlecode.com/svn/site/$NEW_REV/ ./current/
svn commit -m "Updating current site for $NEW_REV release" ./current/
