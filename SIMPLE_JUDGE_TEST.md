# Simple Judge Test - kep.uz kabi

## Test masalasi: A + B

### Masala:
Ikki butun son berilgan. Ularning yig'indisini toping.

### Input:
Birinchi qatorda ikkita butun son a va b (-10^9 ≤ a, b ≤ 10^9)

### Output:
a + b ning qiymatini chiqaring

### Misollar:

**Input 1:**
```
2 3
```
**Output 1:**
```
5
```

**Input 2:**
```
-1 1
```
**Output 2:**
```
0
```

## Test kodlari:

### C++ yechimi:
```cpp
#include <iostream>
using namespace std;

int main() {
    int a, b;
    cin >> a >> b;
    cout << a + b << endl;
    return 0;
}
```

### Java yechimi:
```java
import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt();
        int b = sc.nextInt();
        System.out.println(a + b);
    }
}
```

### Python yechimi:
```python
a, b = map(int, input().split())
print(a + b)
```

### JavaScript yechimi:
```javascript
const readline = require('readline');
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

rl.on('line', (line) => {
    const [a, b] = line.split(' ').map(Number);
    console.log(a + b);
    rl.close();
});
```

## API Test:

### 1. Masala yaratish:
```bash
curl -X POST "http://localhost:8080/api/admin/problems" \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "A + B",
    "difficulty": "EASY",
    "description": "Ikki butun son berilgan. Ularning yig'\''indisini toping.",
    "timeLimit": 1000,
    "memoryLimit": 64,
    "examples": [
      {
        "input": "2 3",
        "output": "5",
        "explanation": "2 + 3 = 5"
      }
    ],
    "testCases": [
      {
        "input": "2 3",
        "expectedOutput": "5"
      },
      {
        "input": "-1 1", 
        "expectedOutput": "0"
      },
      {
        "input": "1000000000 1000000000",
        "expectedOutput": "2000000000"
      }
    ]
  }'
```

### 2. Kod yuborish (C++):
```bash
curl -X POST "http://localhost:8080/api/problems/1/submit" \
  -H "Authorization: Bearer <user_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "#include <iostream>\nusing namespace std;\n\nint main() {\n    int a, b;\n    cin >> a >> b;\n    cout << a + b << endl;\n    return 0;\n}",
    "language": "cpp"
  }'
```

### 3. Kod yuborish (Python):
```bash
curl -X POST "http://localhost:8080/api/problems/1/submit" \
  -H "Authorization: Bearer <user_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "a, b = map(int, input().split())\nprint(a + b)",
    "language": "python"
  }'
```

## Kutilgan natija:

```json
{
  "submissionId": 123,
  "status": "ACCEPTED",
  "message": "Barcha test case'lar o'tdi!",
  "passedTestCases": 3,
  "totalTestCases": 3,
  "executionTime": 45,
  "memoryUsed": 2.1,
  "testResults": [
    {
      "input": "2 3",
      "expectedOutput": "5",
      "actualOutput": "5",
      "status": "PASSED"
    },
    {
      "input": "-1 1",
      "expectedOutput": "0", 
      "actualOutput": "0",
      "status": "PASSED"
    },
    {
      "input": "1000000000 1000000000",
      "expectedOutput": "2000000000",
      "actualOutput": "2000000000", 
      "status": "PASSED"
    }
  ]
}
```

## Xato holatlari:

### Compile Error (C++):
```cpp
#include <iostream>
using namespace std;

int main() {
    int a, b;
    cin >> a >> b;
    cout << a + b << endl; // ; yo'q
    return 0;
}
```

### Wrong Answer:
```cpp
#include <iostream>
using namespace std;

int main() {
    int a, b;
    cin >> a >> b;
    cout << a - b << endl; // - o'rniga +
    return 0;
}
```

### Time Limit Exceeded:
```cpp
#include <iostream>
using namespace std;

int main() {
    int a, b;
    cin >> a >> b;
    
    // Cheksiz loop
    while(true) {
        // hech narsa
    }
    
    cout << a + b << endl;
    return 0;
}
```