
set list "369 377 406"

foreach sn ${list} {
    set cid1      "1128.1.1.${sn}.1.1.1"
    set cid2      "1128.1.1.${sn}.1.1.2"
    set name1     [xvalue asset/meta/daris:pssd-object/name [asset.get :cid ${cid1}]]
    set name2     "${name1} - BMP"
    set source2   [xvalue asset/meta/mf-note/note [asset.get :cid ${cid2}]]
    set source1   [string range $source2 0 [expr {[string length $source2] - 5 }]]
    set imageType    [xvalue asset/meta/vicnode.daris:femur-dataset/image-type [asset.get :cid ${cid2}]]
    set specimenType [xvalue asset/meta/vicnode.daris:femur-dataset/specimen-type [asset.get :cid ${cid2}]]
    
    # update dicom dataset
    asset.set :cid ${cid1} :meta < :vicnode.daris:femur-dataset -tag pssd.meta -ns om.pssd.dataset < :specimen-type ${specimenType} :image-type ${imageType} > :mf-note -tag pssd.meta -ns om.pssd.dataset < :note "${source1}" > >
    om.pssd.object.tag.add :cid ${cid1} :tag < :name ${imageType} > :tag < :name ${specimenType} >
    
    # update bmp dataset
    om.pssd.object.update :id ${cid2} :name ${name2}
}