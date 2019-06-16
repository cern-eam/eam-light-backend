# EAM Light Backend
EAM Light Backend is a web application that provides a REST facade for EAM Light Frontend Application. 

## Configuration
The docker image needs to be parametrized with the following environment variables:


| Variable        | Required?  | Default value |
| ------------- | -----:|---------:|
| EAMLIGHT_INFOR_WS_URL           | **Yes** |  |
| EAMLIGHT_INFOR_TENANT         | **Yes** |  |
| EAMLIGHT_ADMIN_USER   | **Yes** |  |
| EAMLIGHT_ADMIN_PASSWORD   | **Yes** |  |
| EAMLIGHT_INFOR_ORGANIZATION   | **Yes** |  |

You can for instance store your environment variables in a dedicated .env file:

```
EAMLIGHT_INFOR_WS_URL=<url>
EAMLIGHT_INFOR_TENANT=<tenant>
EAMLIGHT_ADMIN_USER=<admin user>
EAMLIGHT_ADMIN_PASSWORD=<password>
EAMLIGHT_INFOR_ORGANIZATION=<org>

```

## Run

The docker container exposes the following ports:

| Port        | Description  |
| ------------- | -----:|
| 8081          | EAM Light Backend | 
| 9090          | JBoss Management Port |

Once you have your own environment variables set up, you can start a new docker container:
```
docker run -p 8081:8081 -p 9090:9090 --env-file .env cerneam/eam-light-backend
``` 

Once the docker container is started, the EAM Light REST backend is available at the endpoint `/apis/rest`

## License
This software is published under the GNU General Public License v3.0 or later.