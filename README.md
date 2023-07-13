# HR_data_collection
> ## Wearable device 실시간 데이터 수집
> - 갤럭시 워치(wearable device) 사용
> - Wear OS 앱

> ## 주요 기능
> 1) 센서 데이터 접근 권한 취득
> 2) 가능한 센서 확인(Heart Rate, Heart Beat, Proximity, Pressure)
> 3) start button - 데이터 수집 시작
> 4) stop button - 데이터 수집 중지
> 5) 내부 저장소에 데이터 실시간 저장

> ## Detail
> - 사용하는 device 마다 수집되는 데이터 다름
> - 수집 데이터 형태: CSV 파일
> - 수집 데이터 저장소: '/data/data/com.example.checksensoravailability/files/SensorData'

> ## Android studio emulator에 갤럭시 워치 연결
> 1) 갤럭시 워치 WIFI와 노트북 WIFI를 같게 연결
> 2) 노트북 환경변수 설정: PATH에 ANDROID HOME 추가
> 3) 갤럭시 워치 '설정' -> '개발자 옵션' -> 'ADB' 와 'WIFI' 켜기 -> 'Wireless Debugging' -> 새로운 장치 추가
> 4) 노트북 cmd -> Android Studio Sdk 디렉토리의 platform-tools 에 cd 사용하여 이동 -> 명령어 'adb pair <ip 주소 : port 넘버>


> ## Reference
> - https://developer.samsung.com/sdp/blog/en-us/2022/05/25/check-which-sensor-you-can-use-in-galaxy-watch-running-wear-os-powered-by-samsung
> - https://developer.android.com/reference/android/hardware/Sensor


