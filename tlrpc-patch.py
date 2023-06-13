'''
 This is the source code of AyuGram for Android.

 We do not and cannot prevent the use of our code,
 but be respectful and credit the original author.

 Copyright @Radolyn, 2023
'''

import re


with open('./TMessagesProj/src/main/java/org/telegram/tgnet/TLRPC.java', encoding='utf-8') as f:
    data = f.read()

r1 = re.compile(r'noforwards = (?P<flags>.+);')
r2 = re.compile(r'restricted = (.+);')
r3 = re.compile(r'history_deleted = (.+);')

data = r1.sub('noforwards = false;\n            ayuNoforwards = \g<flags>;', data)
data = r2.sub('restricted = false;', data)
data = r3.sub('history_deleted = false;', data)

data = data.replace('public boolean noforwards;', 'public boolean noforwards;\n        public boolean ayuNoforwards;')
data = data.replace('public boolean from_scheduled;', 'public boolean from_scheduled;\n        public boolean ayuDeleted;')

with open('./TMessagesProj/src/main/java/org/telegram/tgnet/TLRPC.java', 'w', encoding='utf-8') as f:
    f.write(data)
