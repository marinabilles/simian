#!/bin/bash
printf "Overlap: %d\n" `comm -12 <(cat "$1/failures.xml" | grep \<sequence\> | sort) <(cat "$2/failures.xml" | grep \<sequence\> | sort) | wc -l`

