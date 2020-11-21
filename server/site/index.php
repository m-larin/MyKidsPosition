<?php
	include 'lib.php';
	
	$person = $_GET["id"];
	if ($person){
		$result = getLastPosition($person);	
	}
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Местоположение детей</title>
    <script src="https://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>
    <script type="text/javascript">
        ymaps.ready(init);    
        function init(){ 
			// Карта
            var myMap = new ymaps.Map("map", {
                center: [<?= $result["lat"] ?>, <?= $result["lon"] ?>],
                zoom: 17,
				controls: ['zoomControl']
            }); 
			
			// Метка
			var myPlacemark = new ymaps.Placemark([<?= $result["lat"] ?>, <?= $result["lon"] ?>], {
				balloonContentHeader: "<?= $result["name"] ?>",
				balloonContentBody: "<div><b>Дата: </b><?= $result["date"] ?></div><div><b>Точность: </b><?= $result["accuracy"] ?> м</div><div><b>Заряд: </b><?= $result["batteryLevel"] ?>%</div>",
				hintContent: "Расположение <?= $result["name"] ?>"
			});
			
			myMap.geoObjects.add(myPlacemark);
			
			// Создаем круг.
			var myCircle = new ymaps.Circle(
				// Координаты центра круга.
				[[<?= $result["lat"] ?>, <?= $result["lon"] ?>],
				// Радиус круга в метрах.
				<?= $result["accuracy"] ?>
			], {
				balloonContentHeader: "<?= $result["name"] ?>",
				balloonContentBody: "<div><b>Дата: </b><?= $result["date"] ?></div><div><b>Точность: </b><?= $result["accuracy"] ?> м</div><div><b>Заряд: </b><?= $result["batteryLevel"] ?>%</div>",
				// Содержимое хинта.
				hintContent: "Расположение <?= $result["name"] ?>"
			}, {
				// Задаем опции круга.
				// Включаем возможность перетаскивания круга.
				draggable: false,
				// Цвет заливки.
				fillColor: "#DB7093",
				// Прозрачность заливки 
				fillOpacity: 0.2,
				// Цвет обводки.
				strokeColor: "#990066",
				// Прозрачность обводки.
				strokeOpacity: 0.4,
				// Ширина обводки в пикселях.
				strokeWidth: 1
			});

			// Добавляем круг на карту.
			myMap.geoObjects.add(myCircle);		

			// Перевод в полноэкранный режим
			myMap.container.enterFullscreen();			
		}
    </script>	
  </head>
  <body>
	<div id="map" style="width: 100pt; height: 100pt"></div>
  </body>
</html>
