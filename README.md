#OSPITALITY SERVER

##Intro
###
This is a project that i did while pursuing B.Sc. Honours degree.

#
##Requirements
64-bit architecture 

MySQL (Xampp works fine) (must be accessible from command line)

Java-17

#
##Functionalities
Installer can be downloaded from releases or Intelli-J Idea can be used to run source code.

After Running the Application enter your MySQL user id and password.
(Make sure the MySQL server is up and running.)

On Successful Login into MySQL the Server Application will check for database existence and create the database if it wasn't created already.

The Database Script will be saved in C:\\ospitality folder.

An Account for Administrator will be created Already with below Credentials.

>***Default User ID For logging into client application***
> 
>id: ADM1001
> 
>pass: aa

After Logging in Two buttons are there 

**start server**: to start the ospitality server listening on **PORT 5678**.

**stop server**: will stop the server and disconnect all users connected via client application.

Logs tab on upper left side shows the logs of server by showing incoming request.

