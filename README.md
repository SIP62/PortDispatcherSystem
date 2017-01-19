# PortDispatcherSystem
Многопоточная диспетчерская система, следящую за кораблями в порту.
Реализованы следующие функции:
 У каждого порта есть склад и причалы.
 Корабли швартуются к причалам, после чего могут загрузиться-разгрузиться со склада порта.
 У каждого причала может швартоваться только один корабль, остальные корабли, желающие зайти в порт, должны ждать в очереди.
 Все свои действия корабли логгируют на консоль.
 Раз в пять секунд диспетчерская система выводит в файл (лог-файл) текущее состояние порта (информацию о количестве товаров на складе, корабля, пришвартованных к причалам, кораблях, стоящих в очереди на швартовку).
 * При швартовке корабль указывает порту ее длительность. Диспетчерскаяя система следит за длительностью швартовки корабля, и, если та превышает указанное время, логгирует эту информацию в отдельный журнал.
 **При выделении причала кораблю порт учитывает приоритет корабля, важность и срочность груза, а также нарушал ли корабль правила стоянки в прошлом.
Решение задачи сделано в двух вариантах: первый вариант должен использовать возможности ключевого слова synchronized; второй вариант – возможности библиотеки java.util.concurrent.
