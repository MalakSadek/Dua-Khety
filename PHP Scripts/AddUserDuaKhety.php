<?php
//Creating a connection
    $con= new mysqli("localhost","root","","DuaKhety");

    // Check connection
    if (mysqli_connect_errno())
      {
      echo "Failed to connect to MySQL: " . mysqli_connect_error();
      }

$owner = $_REQUEST["owner"];
$photo = $_REQUEST["photo"];
$code = $_REQUEST["code"];
$description = $_REQUEST["description"];

$sql = "INSERT INTO SocialContent (Owner, Photo, GardinerCode, Description) VALUES ( '$owner', '$photo', '$code', '$description')";

$result = mysqli_query($con, $sql);

if ( false===$result ) {
  printf("error: %s\n", mysqli_error($con));
}

mysqli_close($con);

?>
