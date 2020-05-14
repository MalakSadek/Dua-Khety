<?php
//Creating a connection
  $con= new mysqli("localhost","root","","DuaKhety");
  $code = $_REQUEST['code'];
  $row_result = array();

    // Check connection
    if (mysqli_connect_errno())
      {
      echo "Failed to connect to MySQL: " . mysqli_connect_error();
      }

  $result = mysqli_query($con, "SELECT * FROM GardinerCode WHERE Code = '$code'");

  if($result === FALSE) {
      die(mysqli_error()); // TODO: better error handling
    }

  $row = mysqli_fetch_row($result);

  echo json_encode($row);
    mysqli_close($con);

?>
