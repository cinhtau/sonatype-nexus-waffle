import groovy.json.JsonOutput

// A bunch of security setup related tasks

//
// disable anonymous access 
//
security.setAnonymousAccess(false)
log.info('Anonymous access disabled')

//
// Create new admin user
//
def adminRole = ['nx-admin']
def adminUser = security.addUser('juan.carlos', 'Juan', 'Carlos', 'jc@mimacom.com', true, 'admin456', adminRole)
log.info('User jc created')

//
// Create a new role that allows a user same access as anonymous and adds healtchcheck access
//
def devPrivileges = ['nx-healthcheck-read', 'nx-healthcheck-summary-read']
def anoRole = ['nx-anonymous']
// add roles that uses the built in nx-anonymous role as a basis and adds more privileges
security.addRole('developer', 'Developer', 'User with privileges to allow read access to repo content and healtcheck', devPrivileges, anoRole)
log.info('Role developer created')
// use the new role to create a user 
def devRoles = ['developer']
def johnLegend = security.addUser('john.legend', 'John', 'Legend', 'john.legend@mimacom.com', true, 'changMe456', devRoles)
log.info('User john.legend created')

//
// Create new role that allows deployment and create a user to be used on a CI server
//
// privileges with pattern * to allow any format, browse and read are already part of nx-anonymous
def depPrivileges = ['nx-repository-view-*-*-add', 'nx-repository-view-*-*-edit']
def roles = ['developer']
// add roles that uses the developer role as a basis and adds more privileges
security.addRole('deployer', 'Deployer', 'User with privileges to allow deployment all repositories', depPrivileges, roles)
log.info('Role deployer created')
def depRoles = ['deployer']
def lJenkins = security.addUser('jenkins', 'Leeroy', 'Jenkins', 'leeroy.jenkins@mimacom.com', true, 'changMe789', depRoles)
log.info('User jenkins created')

//
// Change default password
//
def user = security.securitySystem.getUser('admin')
user.setEmailAddress('vinh-vinh@mimacom.com')
security.securitySystem.updateUser(user)
security.securitySystem.changePassword('admin','admin456')
log.info('default password for admin changed')

log.info('Script security completed successfully')

//Return a JSON response containing our new Users for confirmation
return JsonOutput.toJson([adminUser, johnLegend, lJenkins, user])
