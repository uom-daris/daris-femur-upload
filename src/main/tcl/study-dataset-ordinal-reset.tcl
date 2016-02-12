set ns "412 422 424 426 432 434 436 439 440 444 448 449 471 478 479 487 488"
foreach n ${ns} {
    set pid "1128.1.1.${n}.1"
    foreach cid [xvalues cid [asset.query :where cid in '${pid}' :action get-cid :size infinity]] {
        daris.study.dataset.ordinal.reset :cid ${cid}
    }
}
