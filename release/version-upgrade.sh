#!/bin/bash

OLD_VERSION=1.0.1-SNAPSHOT
NEW_VERSION=1.2.0-SNAPSHOT

cd ..

OLD_VERSION_NORMALIZED=$(echo "${OLD_VERSION}" | sed -e 's/[]$.*[\^]/\\&/g' )
NEW_VERSION_NORMALIZED=$(echo "${NEW_VERSION}" | sed -e 's/[]$.*[\^]/\\&/g' )
find . -type f ! -path "./release/version-upgrade.sh" -exec sed -i "s/${OLD_VERSION_NORMALIZED}/${NEW_VERSION_NORMALIZED}/g" {} +
