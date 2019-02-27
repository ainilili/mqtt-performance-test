## 部署
```powershell
docker run -p 8888:1883 -p 8889:8889 -v /var/db/mosca:/db matteocollina/mosca
```