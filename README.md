This is an example how to setup security for Sonatype Nexus Repository Manager OSS.

## Docker

Since it is a proof of concept start Nexus as Docker container.

Sonatype offers an OpenShift compatible docker image. See also [the Docker configuration](https://github.com/sonatype/docker-nexus3).

To start in foreground

```bash
docker run --rm -p 8081:8081 --name nexus sonatype/nexus3
```

You should see some log output like this:

```bash
2019-05-17 12:34:32,242+0000 INFO  [jetty-main-1] *SYSTEM org.sonatype.nexus.bootstrap.osgi.BootstrapListener - Initializing
2019-05-17 12:34:32,244+0000 INFO  [jetty-main-1] *SYSTEM org.sonatype.nexus.bootstrap.osgi.BootstrapListener - Loading OSS Edition
2019-05-17 12:34:32,245+0000 INFO  [jetty-main-1] *SYSTEM org.sonatype.nexus.bootstrap.osgi.BootstrapListener - Installing: nexus-oss-edition/3.16.1.02
2019-05-17 12:34:33,801+0000 INFO  [jetty-main-1] *SYSTEM org.sonatype.nexus.bootstrap.osgi.BootstrapListener - Installed: nexus-oss-edition/3.16.1.02
```

At the end you will see:

```bash
-------------------------------------------------

Started Sonatype Nexus OSS 3.16.1-02

-------------------------------------------------
2019-05-17 12:35:06,950+0000 INFO  [qtp519767623-58] *UNKNOWN org.apache.shiro.session.mgt.AbstractValidatingSessionManager - Enabling session validation scheduler...
2019-05-17 12:35:06,978+0000 INFO  [qtp519767623-58] *UNKNOWN org.sonatype.nexus.internal.security.anonymous.AnonymousManagerImpl - Using default configuration: AnonymousConfiguration{enabled=true, userId='anonymous', realmName='NexusAuthorizingRealm'}
```

As you can see the access for anonymous is allowed.

To change that we are going to configure Nexus with a script. The benefit of a script is, we can redo the configuration from a fresh start.

Another benefit is the easy integration with Ansible.

I use recipes from [the Sonatype Nexus Community](https://github.com/sonatype-nexus-community/nexus-scripting-examples) as start and added some minor adjustments.

## Basics

The `provision.sh` script is the starting point. It contains for instance the default password for the default admin user.

The script calls `addUpdateScript.groovy`.

### Security

`security.groovy` contains all example instructions. The instructions consists of:

- Creating a new admin user named `jc` (Juan Carlos)
- Create Continuous Integration examples
  - Creating a developer role
  - Assign developer role to user `john.legend`
  - Creating a deployer role
  - Assign deployer role to user `jenkins`
- Change default password for default admin

If you change the default password, change also the password in `provision.sh`.


## Test-Run

After you start the Docker container, make `provision.sh` executable and run it.

```bash
# chmod +x provision.sh
# sh provision.sh
```

The resulting output:

```bash
Script named 'security' will be created
Stored scripts are now: [security]

Published security.groovy as security

*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8081 (#0)
* Server auth using Basic with user 'admin'
> POST /service/rest/v1/script/security/run HTTP/1.1
> Host: localhost:8081
> Authorization: Basic YWRtaW46YWRtaW4xMjM=
> User-Agent: curl/7.54.0
> Accept: */*
> Content-Type: text/plain
>
< HTTP/1.1 200 OK
< Date: Fri, 17 May 2019 11:49:17 GMT
< Server: Nexus/3.16.1-02 (OSS)
< X-Content-Type-Options: nosniff
< Content-Type: application/json
< Content-Length: 1199
<
{
  "name" : "security",
  "result" : "[{\"userId\":\"juan.carlos\",\"status\":\"active\",\"roles\":[{\"roleId\":\"nx-admin\",\"source\":\"default\"}],\"firstName\":\"Juan\",\"version\":null,\"lastName\":\"Carlos\",\"emailAddress\":\"jc@mimacom.com\",\"readOnly\":false,\"source\":\"default\",\"name\":\"Juan Carlos\"},{\"userId\":\"john.legend\",\"status\":\"active\",\"roles\":[{\"roleId\":\"developer\",\"source\":\"default\"}],\"firstName\":\"John\",\"version\":null,\"lastName\":\"Legend\",\"emailAddress\":\"john.legend@mimacom.com\",\"readOnly\":false,\"source\":\"default\",\"name\":\"John Legend\"},{\"userId\":\"jenkins\",\"status\":\"active\",\"roles\":[{\"roleId\":\"deployer\",\"source\":\"default\"}],\"firstName\":\"Leeroy\",\"version\":null,\"lastName\":\"Jenkins\",\"emailAddress\":\"leeroy.jenkins@mimacom.com\",\"readOnly\":false,\"source\":\"default\",\"name\":\"Leeroy Jenkins\"},{\"userId\":\"admin\",\"status\":\"active\",\"roles\":[{\"roleId\":\"nx-admin\",\"source\":\"default\"}],\"firstName\":\"Administrator\",\"version\":\"1\",\"lastName\":\"User\",\"emailAddress\":\"vinh-vinh@mimacom.com\",\"readOnly\":false,\"source\":\"default\",\"name\":\"Administrator User\"}]"
* Connection #0 to host localhost left intact
}
Successfully executed security script

Provisioning Scripts Completed
```
