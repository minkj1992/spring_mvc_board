# git 명령어 정리

## commit message convention
> Type: Title(앞문자 대문자)

[Type 관련 문서](https://sujinlee.me/professional-github/)

- Type
  - **feat**: 새로운 기능을 추가할 경우
  - **fix**: 버그를 고친 경우
  - **docs**: 문서 수정한 경우
  - **style**: 코드 포맷 변경, 세미 콜론 누락, 코드 수정이 없는 경우
  - **refactor**: 프로덕션 코드 리팩터링
  - **test**: 테스트 추가, 테스트 리팩터링 (프로덕션 코드 변경 없음)
  - chore: 빌드 테스크 업데이트, 패키지 매니저 설정할 경우 (프로덕션 코드 변경 없음)

[Title 관련 문서](https://blog.ull.im/engineering/2019/03/10/logs-on-git.html)
- Title
  - Fix : 동작하지 않는 부분을 수정 
  - Update : 정상동작하던 내용을 수정, 추가, 보완
  - Add : 파일이 만들어질때
  - Remove : 삭제
  - Refactor : 전면 수정
  - Simplify : Refactor 약한 버전
  - Clean-up : 파일을 폴더에 넣거나 구조 정리할때

- Message Pattern
  - [Title] + A               : A를 title 하다
  - [Title] + A for B         : B를 위해 A추가
  - [Title] + A to B          : B에 A를 추가
  - [Title] + A from B        : B에서 A를
  - [Title] + A instead of B  : B대신 A를 
## remote/develop -> local/minkj1992
    git remote update
    git checkout master
    git rebase master minkj1992

## remote/minkj1992 -> local/minkj1992
    
    git pull origin minkj1992:minkj1992


## add & commit
git 최근 add 취소

    git reset HEAD
    git reset HEAD README.md

git 최신 commit 삭제
    
    git reset HEAD^
    git reset HEAD~2

## commit 합치기
> [git-rebase](https://cjh5414.github.io/git-rebase/)

까먹고 추가로 올리거나, 올라가져있는 commit을 병합하고 싶을 때 사용한다.

    git rebase -i HEAD~2

이후 vi를 통해서 `squash`를 의미하는 `s`를 `pick`대신 사용해주면, pick된 Commit에 `s`가 적용된 commit을 넣어줄 수 있다.