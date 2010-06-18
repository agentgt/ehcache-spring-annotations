#
# Copyright 2010 Nicholas Blair, Eric Dalquist
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

die () {
    echo >&2 "$@"
    exit 1
}

[ "$#" -eq 3 ] || die "3 arguments required, $# provided: OLD_TAG, NEW_TAG, PATH_TO_NEW_SITE"

OLD_REV=$1
NEW_REV=$2
NEW_SITE=$3

# Copy and checkout previous site 
svn cp -m "Creating site for $NEW_REV" https://ehcache-spring-annotations.googlecode.com/svn/site/$OLD_REV https://ehcache-spring-annotations.googlecode.com/svn/site/$NEW_REV
svn co https://ehcache-spring-annotations.googlecode.com/svn/site/$NEW_REV

# Sync the new site over the previous site, removing files that no longer exist
rsync -avr --progress --exclude '.svn'  --delete $NEW_SITE/ ./$NEW_REV/

# Add new files into SVN control, this will generate a lot of 'is already under version control' warnings which can be ignored
find ./$NEW_REV/ -type f | grep -v .svn | xargs svn add --parents

# Some reporting plugins generate output with inconsistent line endings, fix this
find ./$NEW_REV/ -type f | grep -v .svn | xargs dos2unix

# Commit the modified site
svn commit -m "Updating site for $NEW_REV" ./$NEW_REV
