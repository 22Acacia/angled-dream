#!/bin/bash
set -x

#  assumes we are in the project home directory
mvn package
ret=$?
if [ $ret -ne 0 ]; then
  echo "Failed to build project"
  exit $ret
fi

#  write out jar file versions and names to VERSIONS.txt file and save the 
#  application name to an env var
app_name=`ls -1 target/*.jar  | cut -d "/" -f 2 | tee VERSIONS.txt | grep -v original | tail -n 1 | cut -d "-" -f 1`

set +x
echo $GOOGLE_CREDENTIALS > account.json
/opt/google-cloud-sdk/bin/gcloud auth activate-service-account --key-file account.json
set -x

gsutil cp target/*.jar gs://${GSTORAGE_DEST_BUCKET}/${app_name}
ret=$?
if [ $ret -ne 0 ]; then
  echo "Failed to cp jar files to gstorage"
  exit $ret
fi

gsutil acl ch -r -u AllUsers:R gs://$GSTORAGE_DEST_BUCKET/${app_name}
curl -XPOST https://circleci.com/api/v1/project/22acacia/pipeline-examples/tree/master?circle-token=$CIRCLE_TOKEN
