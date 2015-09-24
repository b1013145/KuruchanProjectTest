<?php
        if(isset($_REQUEST['st_lat']) && isset($_REQUEST['st_lng']) && isset($_REQUEST['en_lat']) && isset($_REQUEST['en_lng'])){
                $data1 = (String)$_REQUEST['st_lat'];
                $data2 = (String)$_REQUEST['st_lng'];
                $data3 = (String)$_REQUEST['en_lat'];
                $data4 = (String)$_REQUEST['en_lng'];
                echo $data1, $data2, $data3, $data4;

                $db_kuru = mysql_connect('localhost', 'kurutest', 'kurutest');
                if(!$db_kuru){
                        echo "not connection";
                }

                $db_select = mysql_select_db('ProRSC', $db_kuru);

                if (!$db_select){
                        die('データベース選択失敗です。'.mysql_error());
                }else{
                        $stlat = (DOUBLE)$data1;
                        $stlng = (DOUBLE)$data2;
                        $enlat = (DOUBLE)$data3;
                        $enlng = (DOUBLE)$data4;

                        $select = mysql_query('SELECT st_lat, st_lng, en_lat, en_lng, no_dump, low_dump, high_dump FROM ProRSC');
                        if (!$select) {
                                die('クエリーが失敗しました。'.mysql_error());
                        }
                        while ($row = mysql_fetch_assoc($select)) {
                            print('<p>');
                            print('start_lat='.$row['st_lat']);
                            print(',start_lng='.$row['st_lng']);
                            print(',end_lat='.$row['en_lnt']);
                            print(',end_lng='.$row['en_lng']);
                            print(',no_dump='.$row['no_dump']);
                            print(',low_dump='.$row['low_dump']);
                            print(',high_dump='.$row['high_dump']);
                            print('</p>');
                        }
                }

        }else{
                echo "error!";
        }
?>
