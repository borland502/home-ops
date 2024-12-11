#!/usr/bin/env bash

declare -r SQLITE_BACKUP="${XDG_BIN_HOME}/sqlite_backup.sh"

if ! [ -f "$SQLITE_BACKUP" ]; then
    curl -L https://raw.githubusercontent.com/efrecon/sqlite-backup/master/backup.sh -o "$SQLITE_BACKUP"
    chmod +x "$SQLITE_BACKUP"
else
    if [[ $(sha256sum "$SQLITE_BACKUP") != "de631a26e143a476d9e73d824a42c887d85bf8441710ffb55ad7a020c7bb0a89" ]]; then
        echo "Backup script has changed"
        exit 1
    fi
fi

sqlite_db_ext=$(sudo find / -name "*.db" 2>/dev/null -exec file {} \; | grep "SQLite" | awk -F ':' '{print $1}')
sqlite_sqlite_ext=$(sudo find / -name "*.sqlite" 2>/dev/null \; | awk '{print $NF}')

all_sqlite_files="$sqlite_db_ext $sqlite_sqlite_ext"

mkdir -p "${XDG_CACHE_HOME}/sqlite"
chmod "0700" "${XDG_CACHE_HOME}/sqlite"

for path in $all_sqlite_files; do
    file_name=$(basename "$path")
    sudo "$SQLITE_BACKUP" --keep 2 --dest "${XDG_CACHE_HOME}/sqlite" --output "dump" --name "${file_name}" "$path"
    sudo chown "$(whoami)" -R "${XDG_CACHE_HOME}/sqlite"
    echo "Finished backing up $file_name"
done
