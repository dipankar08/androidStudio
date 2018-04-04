#Data base server
sudo mkdir -p /data/db
sudo mongod --fork --port 23456 --logpath /var/log/mongodb.log

# Webserver
npm install -g wscat 
npm start
