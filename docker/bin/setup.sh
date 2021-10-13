#!/bin/sh
set -x

# ensure $HOME exists and is accessible by group 0 (we don't know what the runtime UID will be)
echo "${USER_NAME}:x:${USER_UID}:0:${USER_NAME} user:${HOME}:/sbin/nologin" >> /etc/passwd
mkdir -p "${HOME}"
chown "${USER_UID}:0" "${HOME}"
chmod ug+rwx "${HOME}"





mkdir -p /usr/local/javalib
chown "${USER_UID}:0" /usr/local/javalib
chmod ug+rwx /usr/local/javalib

chown "${USER_UID}:0" /app.jar
chmod ug+rwx

# no need for this script to remain in the image after running
rm "$0"
sed "s/<POWERFUL_TAG>/$POWERFUL_TAG/g" -i /opt/helm/helm-charts/powerful/values.yaml
