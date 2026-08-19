# Gitlet Design Document

**姓名：** Everlasting

**Made by ChatGPT**

## Classes and Data Structures

### Class 1：`Main`

`Main` 是 Gitlet 的程序入口，主要负责：

* 接收并解析命令行参数；
* 检查参数数量是否合法；
* 将不同命令分发给对应的方法；
* 捕获 `GitletException`，只输出规定的错误信息，而不是打印异常栈。

#### Fields

1. `ISINITED`

    * 表示程序启动时当前目录下是否已经存在 `.gitlet`。
    * 用于判断除了 `init` 之外的命令是否可以执行。

2. `MAXLEN`

    * SHA-1 完整长度，为 `40`。
    * 用于判断一个 commit id 是完整 SHA-1 还是缩写形式。

#### 主要方法

```text
main
init
add
commit
remove
log
globalLog
find
checkout
branch
status
rmBranch
reset
merge
```

---

### Class 2：`Repository`

`Repository` 负责管理 Gitlet 仓库的目录结构，以及与整个仓库或工作目录直接相关的操作，例如：

* 初始化仓库；
* checkout 某个文件；
* status。

#### Fields

1. `CWD`

    * 当前工作目录。

2. `GITLET_DIR`

    * `.gitlet` 目录。

3. `COMMIT_DIR`

    * 保存所有 commit。

4. `BLOB_DIR`

    * 保存所有 blob 文件。

5. `BRANCH_DIR`

    * 保存所有 branch。

6. `MASTER`

    * `master` 分支对应的文件。

7. `STAGING`

    * 保存 staging area。

8. `HEAD`

    * 保存当前所在的 branch 名字。

---

### Class 3：`Commit`

`Commit` 表示某一时刻 Gitlet 仓库的完整快照。

每个 commit 保存：

* commit message；
* 时间；
* 父 commit；
* 当前跟踪的所有文件；
* commit SHA-1；
* merge commit 的第二父节点。

#### Fields

1. `message`

    * commit 信息。

2. `timestamp`

    * commit 创建时间。

3. `parentCommitString`

    * 第一个 parent commit 的 SHA-1。
    * initial commit 中为 `null`。

4. `blobs`

    * 类型：

```java
Map<String, String>
```

表示：

```text
文件名 -> blob SHA-1
```

例如：

```text
a.txt -> 8ac31...
b.txt -> 92df1...
```

这个 Map 表示该 commit **完整跟踪的文件快照**。

5. `secondParentCommitString`

    * merge commit 的第二个 parent commit SHA-1。
    * 普通 commit 为 `null`。

6. `sha`

    * 当前 commit 自己的 SHA-1。

---

### Class 4：`Branch`

`Branch` 本质上是一个：

```text
branch name -> head commit SHA-1
```

的指针。

它还负责：

* 创建分支；
* checkout 分支；
* 删除分支；
* merge；
* split point 查找。

#### Fields

1. `name`

    * branch 名称。

2. `headCommitString`

    * 当前 branch 指向的最新 commit SHA-1。

例如：

```text
master -> abc123...
dev    -> def456...
```

---

### Class 5：`Staging`

`Staging` 表示暂存区，记录下一次 commit 要增加和删除哪些文件。

#### Fields

1. `addFiles`

类型：

```java
Map<String, String>
```

表示：

```text
文件名 -> 暂存版本 blob SHA-1
```

2. `rmFiles`

类型：

```java
Set<String>
```

保存 staged for removal 的文件名。

---

# Algorithms

## 1. Init

执行：

```text
init
```

时：

1. 创建 `.gitlet`。
2. 创建：

    * `commits`
    * `blobs`
    * `branches`
3. 创建空的 staging area。
4. 创建 initial commit：

```text
message = "initial commit"
timestamp = Date(0)
parent = null
blobs = empty
```

5. 创建 `master` branch。
6. 让 `master` 指向 initial commit。
7. 在 `HEAD` 中保存：

```text
master
```



---

## 2. Add

执行：

```text
add <filename>
```

时：

1. 检查 CWD 中该文件是否存在。
2. 根据文件内容计算 SHA-1。
3. 如果该文件之前 staged for removal，则取消 removal。
4. 如果相同版本已经在 addition staging 中，则不进行修改。
5. 如果文件内容与 HEAD commit 中的版本完全相同：

    * 如果之前 staged for addition，则取消 addition；
    * 不继续 stage。
6. 否则加入：

```text
addFiles:
filename -> blobSHA
```

7. 如果 blob 不存在，则保存到：

```text
.gitlet/blobs/<blobSHA>
```

8. 将 Staging 序列化保存。

---

## 3. Remove

执行：

```text
rm <filename>
```

时：

1. 如果文件在 staged additions 中：

    * 从 `addFiles` 删除。

2. 如果 HEAD commit 正在 tracking 该文件：

    * 将文件名加入 `rmFiles`；
    * 如果文件仍存在于 CWD，则删除。

3. 如果文件既没有 staged for addition，也没有被 HEAD tracking：

```text
No reason to remove the file.
```

4. 保存新的 staging area。

---

## 4. Commit

创建新 commit 时：

1. 当前 HEAD commit 作为 parent。
2. 复制 parent commit 的完整 `blobs`：

```text
parent snapshot
```

3. 根据 `rmFiles` 删除对应文件。
4. 根据 `addFiles` 更新对应文件。
5. 如果是 merge commit，则额外记录第二 parent。
6. 根据 Commit 对象计算 SHA-1。
7. 保存：

```text
.gitlet/commits/<commitSHA>
```

8. 当前 branch 移动到新 commit。
9. 清空 staging area。

所以 commit 的核心不是：

> “只记录这一次变化了哪些文件”

而是：

> **保存整个 repository 的完整文件快照。**

---

## 5. Checkout Branch / Reset

这两个操作都使用：

```java
updateWorkingDirectory(currentCommit, targetCommit)
```

基本流程：

1. 检查是否存在会被覆盖的 untracked file。
2. 如果存在，停止操作。
3. 删除 current commit 中存在但 target commit 中不存在的文件。
4. 将 target commit 中所有文件写入 CWD。
5. 清空 staging area。

### Checkout branch

额外修改：

```text
HEAD -> 新 branch name
```

### Reset

不改变当前 branch 名字。

而是：

```text
HEAD
 ↓
current branch
 ↓
target commit
```

也就是移动当前 branch pointer。

---

## 6. Status

`status` 输出：

```text
Branches
Staged Files
Removed Files
Modifications Not Staged For Commit
Untracked Files
```

并按照文件名字典序排序。

### Modifications Not Staged

主要包括：

```text
HEAD tracking 的文件
但 CWD 被修改或删除
```

以及：

```text
已经 staged for addition
但之后 CWD 又被修改或删除
```

### Untracked

一般来说：

```text
CWD 中存在
AND
HEAD 不 tracking
AND
没有 staged for addition
```

另外包括：

```text
已经 staged for removal
但用户又手动在 CWD 创建了同名文件
```

---

## 7. Branch

执行：

```text
branch <name>
```

时：

创建：

```text
name -> current HEAD commit
```

的新 branch。

例如：

```text
master -> C3
```

执行：

```text
branch dev
```

得到：

```text
master -> C3
dev    -> C3
```

之后两个 pointer 可以分别向不同 commit 移动。

删除 branch 时：

> 只删除 branch pointer，不删除 commit。



---

## 8. Split Point

merge 需要找到：

> current branch 与 given branch 的 latest common ancestor。

算法：

1. 从 current commit 开始遍历：

    * first parent
    * second parent

得到：

```text
current ancestors
```

2. 从 given commit 同样遍历：

```text
given ancestors
```

3. 求交集：

```text
common ancestors
```

4. 如果某个 common ancestor 是另一个 common ancestor 的祖先，则它不是 latest。

5. 剩下的最新共同祖先作为 split point。

---

## 9. Merge

执行：

```text
merge <branch>
```

时：

1. 检查 staging 是否为空。
2. 检查 given branch 是否存在。
3. 检查是否 merge 自己。
4. 找到 split point。
5. 处理特殊情况：

    * given 是 current 的祖先；
    * current 是 given 的祖先，执行 fast-forward。
6. 检查是否有 untracked file 会被覆盖。
7. 对每个文件比较三个状态：

```text
split
current
given
```

8. 根据情况决定：

```text
do nothing
take current
take given
remove
conflict
```

9. 所有 merge 产生的修改进入 staging。
10. 创建 merge commit：

```text
first parent  = merge 前 current HEAD
second parent = given HEAD
```

11. 如果发生 conflict：

```text
Encountered a merge conflict.
```



---

## 10. Merge Conflict

发生 conflict 时生成：

```text
<<<<<<< HEAD
<current version>
=======
<given version>
>>>>>>>
```

如果其中一边已经删除文件：

```text
该部分内容为空字符串
```

生成后的 conflict 文件：

1. 写入 CWD；
2. stage for addition；
3. 最终进入 merge commit。

---

# Persistence

Gitlet 的所有持久化数据都保存在：

```text
.gitlet/
```

目录结构：

```text
.gitlet/
├── commits/
│   └── <commit SHA-1>
│
├── blobs/
│   └── <blob SHA-1>
│
├── branches/
│   ├── master
│   └── <branch name>
│
├── staging
│
└── head
```



---

## Commit Persistence

每个 Commit 对象被序列化保存：

```text
.gitlet/commits/<commit SHA-1>
```

例如：

```text
.gitlet/commits/
└── 38ac9e89...
```

文件内部是序列化后的：

```text
Commit
```

对象。

---

## Blob Persistence

普通文件的内容保存在：

```text
.gitlet/blobs/<blob SHA-1>
```

其中：

```text
blob SHA-1 = SHA1(file contents)
```

因此：

> 两个文件只要内容完全一样，就可以共用同一个 blob。



---

## Branch Persistence

每个 branch 保存为：

```text
.gitlet/branches/<branch name>
```

内部序列化保存：

```text
Branch {
    name
    headCommitString
}
```

例如：

```text
.gitlet/branches/master
```

表示：

```text
master -> 某个 commit SHA
```



---

## HEAD Persistence

`HEAD` 不直接保存 commit SHA。

它只保存：

```text
当前 branch 名
```

所以整个关系是：

```text
HEAD
 ↓
Branch
 ↓
Commit
 ↓
filename -> blob SHA
 ↓
Blob contents
```

这是整个 Gitlet 最核心的数据链。

---

## Staging Persistence

Staging 对象序列化保存：

```text
.gitlet/staging
```

内部：

```text
addFiles:
filename -> blob SHA

rmFiles:
filename
```

成功 commit、reset 或 branch checkout 后，会创建新的空 Staging 对象覆盖原来的 staging area。

**Made by ChatGPT**