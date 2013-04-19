#include <stdio.h>

#define COUNT 4

void do_calculate() {
    float t1 = 0.0;
    float t2 = 0.11111;
    int i;
    for(i=0;i<999999999;++i) {
        t1+=t2;
    }
}

int main() {
    int i;
    for(i=0;i<COUNT;++i) {
        do_calculate();
        printf("%d\n", i);
    }
    return 0;
}
