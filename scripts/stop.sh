##!/usr/bin/env bash
#
#PROJECT_ROOT="/home/ubuntu/app"
#JAR_FILE="$PROJECT_ROOT/spring-webapp.jar"
#
#DEPLOY_LOG="$PROJECT_ROOT/deploy.log"
#
#TIME_NOW=$(date +%c)
#
## 현재 구동 중인 애플리케이션 pid 확인
#CURRENT_PID=$(pgrep -f $JAR_FILE)
#
## 프로세스가 켜져 있으면 종료
#if [ -z $CURRENT_PID ]; then
#  echo "$TIME_NOW > 현재 실행중인 애플리케이션이 없습니다" >> $DEPLOY_LOG
#else
#  echo "$TIME_NOW > 실행중인 $CURRENT_PID 애플리케이션 종료 " >> $DEPLOY_LOG
#  kill -15 $CURRENT_PID
#fi


ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

IDLE_PROFILE=$(find_idle_profile)

CONTAINER_ID=$(docker container ls -f "name=${IDLE_PROFILE}" -q)

echo "> 컨테이너 ID는 무엇?? ${CONTAINER_ID}"
echo "> 현재 프로필은 무엇?? ${IDLE_PROFILE}"

if [ -z ${CONTAINER_ID} ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> docker stop ${IDLE_PROFILE}"
  sudo docker stop ${IDLE_PROFILE}
  echo "> docker rm ${IDLE_PROFILE}"
  sudo docker rm ${IDLE_PROFILE}    # 컨테이너 이름을 지정해서 사용하기 때문에.. 꼭 컨테이너 삭제도 같이 해주셔야 합니다. (나중에 다시 띄울거기 때문에..)
  sleep 5
fi