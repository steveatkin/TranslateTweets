Get started with ${app}
-----------------------------------
Welcome to Translate Tweets application!

This sample application demonstrates how to use the Watson Machine and Language Identification services.

1. [Install the cf command-line tool](${doc-url}/#starters/BuildingWeb.html#install_cf).
2. [Download the Translate Tweets application package]https://hub.jazz.net/project/steveatkin/TranslateTweets/overview.
3. Extract the package and `cd` to it.
4. Connect to Bluemix:

		cf api ${api-url}

5. Log into Bluemix:

		cf login -u ${username}
		cf target -o ${org} -s ${space}
				
6. Compile the Java code and generate the war package using ant.
7. Deploy your app:

		cf push ${app} -p translate.war

8. Access your app: [http://${route}](http://${route})
