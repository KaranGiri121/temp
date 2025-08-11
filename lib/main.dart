import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:logger/logger.dart';
import 'package:tempss/pagination_bloc.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    var color = [
      Colors.red,
      Colors.green,
      Colors.brown,
      Colors.amber,
      Colors.deepPurple,
      Colors.pink,
    ];
    return MaterialApp(
      home: BlocProvider(
        create: (context) => PaginationBloc()..add(FetchData()),
        child: BlocBuilder<PaginationBloc, PaginationState>(
          buildWhen:
              (previous, current) =>
                  (current is PaginationLoading) || current is PaginationLoaded,
          builder: (context, state) {
            return Scaffold(
              body: NotificationListener<ScrollNotification>(
                onNotification: (notification) {
                  if(notification.metrics.pixels==notification.metrics.maxScrollExtent){
                    if(state is !PaginationLoading){
                      Logger().e("Hit");
                      BlocProvider.of<PaginationBloc>(
                        context,
                        listen: false,
                      ).add(FetchData());
                    }
                  }
                  return true;
                },
                child: ListView.separated(
                  itemBuilder: (context, index) {
                    if (index ==
                        BlocProvider.of<PaginationBloc>(
                          context,
                          listen: true,
                        ).all.length) {
                      return (state is PaginationLoading)
                          ? Center(child: CircularProgressIndicator())
                          : SizedBox.shrink();
                    }
                    var temp = BlocProvider.of<PaginationBloc>(
                      context,
                    ).all[index];
                    return Container(
                      height: 200,
                      color: color[Random().nextInt(color.length - 1)],
                      child: Text(
                        temp.firstName+temp.id.toString()
                      ),
                    );
                  },
                  separatorBuilder: (context, index) => SizedBox(height: 24),
                  itemCount:
                      BlocProvider.of<PaginationBloc>(
                        context,
                        listen: true,
                      ).all.length +
                      1,
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}
