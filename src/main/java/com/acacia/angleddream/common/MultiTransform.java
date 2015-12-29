package com.acacia.angleddream.common;


import com.acacia.sdk.AbstractTransform;
import com.acacia.sdk.AbstractTransformComposer;
import com.google.cloud.dataflow.sdk.transforms.PTransform;
import com.google.cloud.dataflow.sdk.transforms.ParDo;
import com.google.cloud.dataflow.sdk.values.PCollection;
import com.google.cloud.dataflow.sdk.values.PCollectionTuple;
import com.google.cloud.dataflow.sdk.values.TupleTag;
import com.google.cloud.dataflow.sdk.values.TupleTagList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class MultiTransform extends PTransform<PCollection<String>, PCollection<String>> {

    private ServiceLoader<AbstractTransformComposer> loader;
    final TupleTag<String> mainOutput = new TupleTag<>();
    final TupleTag<String> errorOutput = new TupleTag<>();
    private List<TupleTag> tagList = new ArrayList<>();


    private static final Logger LOG = LoggerFactory.getLogger(MultiTransform.class);

    public MultiTransform(){

        loader = ServiceLoader.load(AbstractTransformComposer.class);
        tagList.add(errorOutput);

    }

    @Override
    public PCollection<String> apply(PCollection<String> item) {

        PCollection<String> tmp = item;
        PCollectionTuple results;


        Iterator<AbstractTransformComposer> transforms = loader.iterator();



        while (transforms.hasNext()) {

            AbstractTransformComposer f =  transforms.next();

        //    System.out.println("Composer: " + f.getClass().getCanonicalName());

            if(f.getOrderedTransforms() != null)
          //      System.out.println("OrderedTransforms: " + f.getOrderedTransforms().size());



            for(AbstractTransform t : f.getOrderedTransforms()) {


            //    System.out.println("Applying: " + t.getClass().getCanonicalName());
              //  System.out.println("Input: " + item);
      //          results = tmp.apply(ParDo.named(tmp.getName()).withOutputTags(mainOutput, TupleTagList.of(errorOutput)).of(t));

                tmp = tmp.apply(ParDo.named(tmp.getName()).of(t));

                //System.out.println("Output: " + tmp);

             //   tmp = results.get(mainOutput);

                //how to also return error?

            }

        }



        return tmp;

    }

}

