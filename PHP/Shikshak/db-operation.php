<?php

use \Slim\App;

require 'vendor/autoload.php';
require 'lib/mysql.php';

$app = new App();

$app->get('/', 'getUsers');

 $app->post('/signin/{email}', function($request, $response, $args) {
     getSignIn($args['email']);
 });

$app->post('/add', function($request, $response, $args) {
    addUser($request->getParsedBody());//Request object’s <code>getParsedBody()</code> method to parse the HTTP request 
});

$app->post('/login', function($request, $response, $args) {
    getLogIn($request->getParsedBody());//Request object’s <code>getParsedBody()</code> method to parse the HTTP request 
});

$app->post('/addCoaching', function($request, $response, $args) {
    addCoaching($request->getParsedBody());//Request object’s <code>getParsedBody()</code> method to parse the HTTP request 
});

$app->post('/addBatch', function($request, $response, $args) {
    addBatch($request->getParsedBody());//Request object’s <code>getParsedBody()</code> method to parse the HTTP request 
});

 $app->post('/getCoaching/{email}', function($request, $response, $args) {
     getCoaching($args['email']);
 });

  $app->post('/getBatch/{email}', function($request, $response, $args) {
     getBatch($args['email']);
 });

$app->post('/getTeacher', function($request, $response, $args) {
    getTeacher($request->getParsedBody());//Request object’s <code>getParsedBody()</code> method to parse the HTTP request 
});

$app->post('/getStudent', function($request, $response, $args) {
    getStudent($request->getParsedBody());//Request object’s <code>getParsedBody()</code> method to parse the HTTP request 
});

$app->post('/getCity', function($request, $response, $args) {
    getCity($request->getParsedBody());//Request object’s <code>getParsedBody()</code> method to parse the HTTP request 
});

$app->post('/getColony', function($request, $response, $args) {
    getColony($request->getParsedBody());//Request object’s <code>getParsedBody()</code> method to parse the HTTP request 
});

$app->post('/getSubject', function($request, $response, $args) {
    getSubject($request->getParsedBody());//Request object’s <code>getParsedBody()</code> method to parse the HTTP request 
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

function getCoaching($email) {
    $db = connect_db();
    $sql = "SELECT name FROM coaching WHERE `owner` = '$email' ORDER BY `name`";
    $exe = $db->query($sql);
    $data = $exe->fetch_all(MYSQLI_ASSOC);
    $db = null;
    echo json_encode($data);
}

function getBatch($email) {
    $db = connect_db();
    $sql = "SELECT batch_name FROM batch WHERE `email` = '$email' ORDER BY `batch_name`";
    $exe = $db->query($sql);
    $data = $exe->fetch_all(MYSQLI_ASSOC);
    $db = null;
    echo json_encode($data);
}

function getCity() {
    $db = connect_db();
    $sql = "SELECT name FROM city order by name";
    $exe = $db->query($sql);
    $data = $exe->fetch_all(MYSQLI_ASSOC);
    $db = null;
    echo json_encode($data);
}

function getColony() {
    $db = connect_db();
    $sql = "SELECT DISTINCT name FROM colony order by name";
    $exe = $db->query($sql);
    $data = $exe->fetch_all(MYSQLI_ASSOC);
    $db = null;
    echo json_encode($data);
}

function getSubject() {
    $db = connect_db();
    $sql = "SELECT DISTINCT subject_name FROM subject order by subject_name";
    $exe = $db->query($sql);
    $data = $exe->fetch_all(MYSQLI_ASSOC);
    $db = null;
    echo json_encode($data);
}

function getSignIn($email) {
    $db = connect_db();
    $sql = "SELECT id FROM user WHERE `email` = '$email'";
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

function getLogIn($data) {
    $db = connect_db();
    $sql = "SELECT id FROM user WHERE `email` = '$data[email]' AND `password` = '$data[password]'";
    $exe = $db->query($sql);
    $data = $exe->fetch_all(MYSQLI_ASSOC);
    $db = null;
    if (!empty($data))
        echo json_encode($data);
    else
        echo "false";
}

function addCoaching($data) {
    $db = connect_db();
    $sql = "insert into coaching (name,colony,city,address,owner)"
            . " VALUES('$data[name]','$data[colony]','$data[city]','$data[address]','$data[owner]')";
    $exe = $db->query($sql);
    $last_id = $db->insert_id;
    $db = null;
    if (!empty($last_id))
        echo "true";
    else
        echo "false";
}

function addBatch($data) {
    $db = connect_db();
    $sql = "insert into batch (batch_name,batch_sub,batch_time,batch_fee,coaching_name,email)"
            . " VALUES('$data[batch_name]','$data[batch_sub]','$data[batch_time]','$data[batch_fee]','$data[coaching_name]','$data[email]')";
    $exe = $db->query($sql);
    $last_id = $db->insert_id;
    $db = null;
    if (!empty($last_id))
        echo "true";
    else
        echo "false";
}

function getTeacher($data) {
    $db = connect_db();
    $sql = "select fname, email from user where email = (select email from user_request where relation = 'teacher' AND batch = (select batch_id from batch where batch_name = '$data[batch_name]'))";
    // join
    $exe = $db->query($sql);
    $data = $exe->fetch_all(MYSQLI_ASSOC);
    $db = null;
    echo json_encode($data);
}

function getStudent($data) {
    $db = connect_db();
// join    $sql = "select fname, email from user where email = (select email from user_request where relation = 'student' AND batch = (select batch_id from batch where batch_name = '$data[batch_name]'))";
    $exe = $db->query($sql);
    $data = $exe->fetch_all(MYSQLI_ASSOC);
    $db = null;
    echo json_encode($data);
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