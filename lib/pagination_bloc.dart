import 'dart:convert';
import 'dart:math';

import 'package:bloc/bloc.dart';
import 'package:logger/logger.dart';
import 'package:meta/meta.dart';
import 'package:http/http.dart' as http;
import 'package:tempss/model.dart';
import 'package:flutter/material.dart';

part 'pagination_event.dart';

part 'pagination_state.dart';

class PaginationBloc extends Bloc<PaginationEvent, PaginationState> {
  var pageNumber = 1;
  var maxNumber = 1;
  List<User> all = [];

  PaginationBloc() : super(PaginationInitial()) {
    on<FetchData>((event, emit) async {
      if(pageNumber>maxNumber || state is PaginationLoading){
        return ;
      }
      emit(PaginationLoading());

      var response = await http.get(
        Uri.parse("https://jsonplaceholder.typicode.com/posts?_limit=5&_page=$pageNumber"),
      );

      Logger().e(response.body);
      if (response.statusCode == 200) {
        var jsonBody = jsonDecode(response.body);
        maxNumber = 20;
        var temp = <User>[];
        for (var i in jsonBody) {
          temp.add(User.fromJson(i));
        }
        all.addAll(temp);
      }
      pageNumber++;
      emit(PaginationLoaded());
    });
  }
}
