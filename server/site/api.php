<?php
	include 'lib.php';

	// Разбираем запрос
	$json = file_get_contents('php://input');
	$command = json_decode($json);
	
	// Выполняем команду
	if ($command->action == "get_last_position"){
		$result = getLastPosition($command->person);	
	}else{
		// TODO $command->action == 'save_position'
		$result = savePosition($command->date, $command->accuracy, $command->lat, $command->lon, $command->batteryLevel, $command->person);	
	}
	
	header('Content-Type: application/json; charset=UTF-8');
	echo json_encode($result);
?>
