<?php
        if(isset($_REQUEST['st_lat']) && isset($_REQUEST['st_lng']) && isset($_REQUEST['en_lat'])
                && isset($_REQUEST['en_lng']) && isset($_REQUEST['no_dump']) && isset($_REQUEST['low_dump']) && isset($_REQUEST['high_dump'])){
                $data1 = (String)$_REQUEST['st_lat'];
                $data2 = (String)$_REQUEST['st_lng'];
                $data3 = (String)$_REQUEST['en_lat'];
                $data4 = (String)$_REQUEST['en_lng'];
                $data5 = (String)$_REQUEST['no_dump'];
                $data6 = (String)$_REQUEST['low_dump'];
                $data7 = (String)$_REQUEST['high_dumo'];
                echo $data1, $data2, $data3, $data4, $data5, $data6, $data7;

                $db_kuru = mysql_connect('localhost', 'kurutest', 'kurutest');
                if(!$db_kuru){
                        echo "not connection";
                }

                $db_select = mysql_select_db('LowRSC', $db_kuru);
                if (!$db_select){
                        die('データベース選択失敗です。'.mysql_error());
                }else{
                        $stlat = (DOUBLE)$data1;
                        $stlng = (DOUBLE)$data2;
                        $enlat = (DOUBLE)$data3;
                        $enlng = (DOUBLE)$data4;
                        $n_d = (INT)$data5;
                        $l_d = (INT)$data6;
                        $h_d = (INT)$data7;
                        $sql1 = sprintf("INSERT INTO latlong (st_lat, st_lng, en_lat, en_lng, no_dump, low_dump, high_dump)
                                VALUES (%9.7f, %10.7f, %9.7f, %10.7f, %d, %d, %d)", $stlat, $stlng, $enlat, $enlng, $n_d, $l_d, $h_d);
                        $result = mysql_query($sql1);
                }
        }else{
                echo "error!";
        }
?>
