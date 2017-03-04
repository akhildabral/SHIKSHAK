<?php

use \Slim\App;

require 'vendor/autoload.php';
require 'lib/mysql.php';

$app = new App();

$app->get('/', 'getUsers');

 $app->post('/signup/{email}', function($request, $response, $args) {
     getSignUp($args['email']);
 });

$app->post('/add', function($request, $response, $args) {
    addUser($request->getParsedBody());//Request object’s <code>getParsedBody()</code> method to parse the HTTP request 
});

$app->post('/signin', function($request, $response, $args) {
    getSignIn($request->getParsedBody());//Request object’s <code>getParsedBody()</code> method to parse the HTTP request 
});

$app->put('/update_employee', function($request, $response, $args) {
    update_employee($request->getParsedBody());
});

$app->delete('/delete_employee', function($request, $response, $args) {
    delete_employee($request->getParsedBody());
});

$app->run();

function getUsers() {
    $db = connect_db();
    $sql = "SELECT * FROM user ORDER BY `fname`";
    $exe = $db->query($sql);
    $data = $exe->fetch_all(MYSQLI_ASSOC);
    $db = null;
    echo json_encode($data);
}

function getSignUp($email) {
    $db = connect_db();
    $sql = "SELECT account FROM user WHERE `email` = '$email'";
    $exe = $db->query($sql);
    $data = $exe->fetch_all(MYSQLI_ASSOC);
    $db = null;
    if (!empty($data))
        echo json_encode($data);
    else
        echo "false";
}

function addUser($data) {
    $db = connect_db();
    $sql = "insert into user (fname,lname,password,phone,account,picture,email)"
            . " VALUES('$data[fname]','$data[lname]','$data[password]','$data[phone]','$data[account]','$data[picture]','$data[email]')";
    $exe = $db->query($sql);
    $last_id = $db->insert_id;
    $db = null;
    if (!empty($last_id))
        echo "true";
    else
        echo "false";
}

function getSignIn($data) {
    $db = connect_db();
    $sql = "SELECT account FROM user WHERE `email` = '$data[email]' AND `password` = '$data[password]'";
    $exe = $db->query($sql);
    $data = $exe->fetch_all(MYSQLI_ASSOC);
    $db = null;
    if (!empty($data))
        echo json_encode($data);
    else
        echo "false";
}

function update_employee($data) {
    $db = connect_db();
    $sql = "update employee SET emp_name = '$data[emp_name]',emp_contact = '$data[emp_contact]',emp_role='$data[emp_role]'"
            . " WHERE employee_id = '$data[employee_id]'";
    $exe = $db->query($sql);
    $last_id = $db->affected_rows;
    $db = null;
    if (!empty($last_id))
        echo $last_id;
    else
        echo false;
}

function delete_employee($employee) {
    $db = connect_db();
    $sql = "DELETE FROM employee WHERE employee_id = '$employee[employee_id]'";
    $exe = $db->query($sql);
    $db = null;
    if (!empty($last_id))
        echo $last_id;
    else
        echo false;
}

?>