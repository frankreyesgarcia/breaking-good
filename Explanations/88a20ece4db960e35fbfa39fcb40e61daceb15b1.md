CI detected that the dependency upgrade from version **google-cloud-pubsublite-0.6.0** to **google-cloud-pubsublite-1.6.3** has failed. Here are details to help you understand and fix the problem:
1. Your client utilizes **7** instructions which has been modified in the new version of the dependency.
   * <details>
        <summary>Method <b>setPartition()</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          

          </details>
            
        To resolve this issue, there are alternative options available in the new version of the dependency that can replace the incompatible method currently used in the client. You can consider substituting the existing method with one of the following options provided by the new version of the dependency
        ``` java
        CommitterSettings$Builder setPartition(Partition);
        ```
     </details>
   * <details>
        <summary>Method <b>setLocation()</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          

          </details>
            
        To resolve this issue, there are alternative options available in the new version of the dependency that can replace the incompatible method currently used in the client. You can consider substituting the existing method with one of the following options provided by the new version of the dependency
        ``` java
        ReservationPath$Builder setLocation(CloudRegion);
        ```
     </details>
   * <details>
        <summary>Method <b>build()</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          

          </details>
            
        To address this incompatibility, there are 2 alternative options available in the new version of the dependency that can replace the incompatible method currently used in the client. You can consider substituting the existing method with one of the following options provided by the new version of the dependency:
        ``` java
        SubscriberSettings build();
        ```
        ``` java
        AssignerSettings build();
        ```
     </details>
   * <details>
        <summary>Method <b>of()</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          

          </details>
            
        To resolve this issue, there are alternative options available in the new version of the dependency that can replace the incompatible method currently used in the client. You can consider substituting the existing method with one of the following options provided by the new version of the dependency
        ``` java
        CloudRegionOrZone of(CloudRegion);
        ```
     </details>
   * <details>
        <summary>Method <b>setContext()</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          

          </details>
            
     </details>
   * <details>
        <summary>Class <b>com.google.cloud.pubsublite.PublishMetadata</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          
            *   >[[ERROR] /java-pubsub-group-kafka-connector/src/main/java/com/google/pubsublite/kafka/sink/PublisherFactoryImpl.java:[36,20] cannot find symbol](https://github.com/chains-project/breaking-good/actions/runs/8110103454/job/22166641300#step:4:1309)
            *   An error was detected in line 36 which is making use of an outdated API.
             ``` java
             36   com.google.cloud.pubsublite.PublishMetadata;
            ```

          </details>
            
     </details>
   * <details>
        <summary>Method <b>newBuilder()</b> which has been <b>removed</b> in the new version of the dependency</summary>
            
        * <details>
          <summary>The failure is identified from the logs generated in the build process. </summary>
          

          </details>
            
        To resolve this issue, there are alternative options available in the new version of the dependency that can replace the incompatible method currently used in the client. You can consider substituting the existing method with one of the following options provided by the new version of the dependency
        ``` java
        AssignerSettings$Builder newBuilder();
        ```
     </details>

