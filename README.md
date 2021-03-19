# EAM Light Backend
EAM Light Backend provides a REST facade for the [eam-light-frontend](https://github.com/cern-eam/eam-light-frontend) module. Together, they constitute the EAM Light web application. 

## Run
To run EAM Light using Docker, we have available a Docker image that exposes both the backend and frontend modules of the application.

The docker container may be started by providing a single argument: the Infor Web Server URL (EAMLIGHT_INFOR_WS_URL), which will look similar in your organization to the following: `https://inforappserver/axis/services/EWSConnector`.
```
docker run -p 8080:8080 -p 9090:9090 --env EAMLIGHT_INFOR_WS_URL=<Infor WS URL> cerneam/eam-light
```

In addition, you have to create a new Grid (Administration / Screen Configuration / Grid Designer) exactly as illustrated below, with the exception of the Grid ID. 

![Alt text](docs/EAMLight_Layout_Grid.png?raw=true "EAM Light Layout Grid")

Once the docker container is started, browsing to [http://localhost:8080/eamlight](http://localhost:8080/eamlight) will open the standard login screen of EAM Light.

The docker container exposes the following ports:

| Port          | Description           |
| ------------- | ---------------------:|
| 8080          | EAM Light Backend     | 
| 9090          | JBoss Management Port |

The EAM Light REST backend is available at the endpoint [http://localhost:9090/rest](http://localhost:9090/rest).

You can find out how to further configure the backend below, and the frontend by reading its [documentation](https://github.com/cern-eam/eam-light-frontend).

## Configuration
The docker image is parametrizable with the following environment variables:

| Variable                      | Required    | Default Value | Example    |
| ----------------------------- | ----------: | ------------- | ---------- |
| EAMLIGHT_INFOR_WS_URL         | **Yes**     |               | https://cmmsx.cern.ch/axis/services/EWSConnector |
| EAMLIGHT_ADMIN_USER           | **No**      |               | R5         |
| EAMLIGHT_ADMIN_PASSWORD       | **No**      |               | test123    |
| EAMLIGHT_AUTHENTICATION_MODE  | **No**      | STD           | STD        |
| EAMLIGHT_DEFAULT_USER         | **No**      |               | LPATER     |
| EAMLIGHT_INFOR_TENANT         | **No**      |               | infor      |
| EAMLIGHT_INFOR_ORGANIZATION   | **No**      |               | *          |

The EAMLIGHT_AUTHENTICATION_MODE can be set to the following values to change how authentication is performed:

| EAMLIGHT_AUTHENTICATION_MODE | Usage |
| ---------------------------- | ----- |
| STD                          | Displays a login screen    |
| LOCAL                        | Uses EAMLIGHT_DEFAULT_USER, EAMLIGHT_ADMIN_PASSWORD, EAMLIGHT_INFOR_TENANT and EAMLIGHT_INFOR_ORGANIZATION to login automatically |
| SSO                          | Logins using SSO with ADFS |
| OPENID                       | Logins using OPENID        |

The SSO and OPENID options can be used to disable the standard login screen, so that you can used the shared authentication schema of your enterprise. Please contact us if you wish to configure this option.

You may store your environment variables in a dedicated .env file, so that they persist:
```
EAMLIGHT_INFOR_WS_URL=<url>
EAMLIGHT_ADMIN_USER=<admin user>
EAMLIGHT_ADMIN_PASSWORD=<password>
```

To use this file in Docker, run it using the `--env-file` option:
```
docker run -p 8080:8080 -p 9090:9090 --env-file .env cerneam/eam-light
```

## License
This software is published under the GNU General Public License v3.0 or later.