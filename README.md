first-step: CSVを読み込み、コンソールに出力します  
use-item-processor: first-stepにItemProcessorを追加して、フルネームを含むdomainに変換します  
use-jdbc-item-writer: JdbcItemWriterを利用してSQLでデータを書き込みます  
use-hibernate-validation: annotationによるvalidationを行い、エラーとなったレコードをskipします  
separate-business-data-source: メタデータ用のDataSourceとビジネス用のDataSourceを分離しました  
use-jpa-item-writer: JPAを利用してデータを書き込みます 
use-routing-datasource: マルチテナントを想定し、CSVファイルごとに接続するデータベースを切り替えるようにしました  
use-container: バッチをコンテナで動かすようにしました  
