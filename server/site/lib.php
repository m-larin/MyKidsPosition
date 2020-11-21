<?php

function connect(){
	$mysqli = new mysqli('localhost', 'mkp', 'mkp', 'mkp');

	/*
	 * Это "официальный" объектно-ориентированный способ сделать это
	 * однако $connect_error не работал вплоть до версий PHP 5.2.9 и 5.3.0.
	 */
	if ($mysqli->connect_error) {
		die('Ошибка подключения (' . $mysqli->connect_errno . ') ' . $mysqli->connect_error);
	}

	return $mysqli;
}

function savePosition($date, $accuracy, $lat, $lon, $batteryLevel, $person){
	$connection = connect();
	
	/* создаем подготавливаемый запрос */
	if ($stmt = $connection->prepare("insert into position (date, accuracy, lat, lon, battery_level, person_id) values (?, ?, ?, ?, ?, ?)")) {

		/* связываем параметры с метками */
		$stmt->bind_param("siddii", $date, $accuracy, $lat, $lon, $batteryLevel, $person);

		/* запускаем запрос */
		if (!$stmt->execute()){
			die('Ошибка выполнения запроса (' . $connection->errno . ') ' . $connection->error);			
		}

		/* закрываем запрос */
		$stmt->close();
	}else{
		die('Ошибка подготовки запроса (' . $connection->errno . ') ' . $connection->error);			
	}
	
	$connection->close();
	$result["result"] = 'OK';
	return $result;
}

function getLastPosition($person){
	$connection = connect();
	
	/* создаем подготавливаемый запрос */
	if ($stmt = $connection->prepare("select date, accuracy, lat, lon, battery_level, u.name from position p join person u on p.person_id = u.id where person_id = ? order by p.id desc limit 1")) {

		/* связываем параметры с метками */
		$stmt->bind_param("i", $person);

		/* запускаем запрос */
		if (!$stmt->execute()){
			die('Ошибка выполнения запроса (' . $connection->errno . ') ' . $connection->error);			
		}

		/* связываем переменные с результатами запроса */
		$stmt->bind_result($date, $accuracy, $lat, $lon, $batteryLevel, $name);

		/* получаем значения */
		$stmt->fetch();

		/* закрываем запрос */
		$stmt->close();
	}else{
		die('Ошибка подготовки запроса (' . $connection->errno . ') ' . $connection->error);			
	}
	
	$connection->close();
	
	$result["date"] = $date;
	$result["accuracy"] = $accuracy;
	$result["lat"] = $lat;
	$result["lon"] = $lon;
	$result["batteryLevel"] = $batteryLevel;
	$result["name"] = $name;
	return $result;
}

?>
