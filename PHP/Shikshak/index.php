<?php

require 'vendor/autoload.php';
use \Slim\App;

$app = new App();
//slim application routes

$app->get('/', function ($request, $response, $args) { 
 $response->write("Welcome to Shikshak");
 return $response;
});

$app->run();

?>