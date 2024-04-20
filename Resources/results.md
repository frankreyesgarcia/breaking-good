| Breaking commit                          | Maracas Result                            | Breaking Good Result                                                                         | Description                                                                                                                                                                   |
|:-----------------------------------------|:------------------------------------------|:---------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| bd3ce213e2771c6ef7817c80818807a757d4e94a | Breaking changes: 379<br/> Broken use: 0  | Method modifies but needs to generate an explanation with an exception                       | -------------------                                                                                                                                                           |
| 43b3a858b77ec27fc8946aba292001c3de465012 | Breaking changes: 244 <br/> Broken use: 8 | The error is not related to any of the dependency changes                                    | Check if it is related to a transitive dependency<br/>Analyze why the new version of the dependency <br/>causes a break with a transitive dependency                          |                                   
| 8e1f0f08eef839903067c7c11432117c4897d0cd | Breaking changes: 0 <br/> Broken use: 0   | The issue occurs because the project is compiled with a different version of Java            | -------------------                                                                                                                                                           |
| aa14451c6f218af9c08e846345d83259eb7d46a8 | Broken use: 0                             | The issue occurs because the project is compiled with a different version of Java            | -------------------                                                                                                                                                           |
| aa14451c6f218af9c08e846345d83259eb7d46a8 | Broken use: 0                             | The issue occurs because the dependency version is compiled with a different version of Java | [ERROR] /LPVS/src/main/java/com/lpvs/util/FileUtil.java:[13,32] cannot access org.springframework.util.FileSystemUtils <br/>class file has wrong version 61.0, should be 55.0 |
| 6c53cd904bd66fc79af8687571e607c259226b81 | Broken use: 4                             | The issue occurs because the project is compiled with a different version of Java            | -------------------                                                                                                                                                           |
| 6c53cd904bd66fc79af8687571e607c259226b81 | broken use:  4                            | No relationship between JAPICMP changes and the log errors                                   |                                                                                                                                                                               |
| 26fd1cd7639b7deb7078df5e4cb05c6d463ad07a | Broken use: 0                             | Related to a third-party dependency                                                          |                                                                                                                                                                               |
| 6c9a2ecf3bac1e0c7675e03b2828a71450d8ed45 | Broken use: 0                             | No changes related to JAPICMP results                                                        |                                                                                                                                                                               |
| c5fd5187ce64d2b53602717f09cc18dd21d55e8d | ----                                      | Use of the `-Werror` option which treats warnings as errors                                  |                                                                                                                                                                               |
| 7d97e1c7331f6722eb1d8192bf0a2686f5a33798 | Broken use : 0                            | No relationship between japicmp and the errors                                               |                                                                                                                                                                               |
| c44aa1857cdeb811d87303df5671be9431105d3c | Use of the `-Werror`                      | Warning errors are not present in the results of japicmp                                     |
| db02c6bcb989a5b0f08861c3344b532769530467 | Broken use:18                             | The error is not related to any of the dependency changes                                    | Check if it is related to a transitive dependency<br/>Analyze why the new version of the dependency <br/>causes a break with a transitive dependency                          |
| 7e8c62e2bb21097e563747184636cf8e8934ce98 | Broken use: 0                             | The issue is with a third-party dependency                                                   | -------------------                                                                                                                                                           |
| 3a4a2b11483689ca3e99e92785a7b27c56d072b8 | Broken use: 0                             | Related to a third-party dependency                                                          |                                                                                                                                                                               |
| c09896887acf0fe59320e01145a7034cd8d4e326 | Broken use : 4                            | fixed                                                                                        | FIXED                                                                                                                                                                         |